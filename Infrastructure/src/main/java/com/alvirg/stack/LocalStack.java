package com.alvirg.stack;

import com.amazonaws.services.licensemanager.model.LicenseStatus;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props){
        super(scope, id, props);

        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabase("PatientServiceDB", "patient-service-db");

        CfnHealthCheck authDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");
        CfnHealthCheck patientDbHealthCheck = createDbHealthCheck(patientServiceDb, "PatientServiceDBHealthCheck");

        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();

        FargateService authService =
                createFargateService("AuthService",
                        "auth-service",
                        List.of(8088),
                        authServiceDb,
                        Map.of("JWT_SECRETE", "sRilhKJvu9aLQbAq3v3VE9X3GyMEKMHOmhhTRygSo14E"));
        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        FargateService billingService =
                createFargateService("BillingService",
                        "billing-service",
                        List.of(8086, 9096),
                        null,
                        null);

        FargateService analyticsService =
                createFargateService("AnalyticsService",
                        "analytics-service",
                        List.of(8087),
                        null,
                        null);
        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService =
                createFargateService("PatientService",
                        "patient-service",
                        List.of(8085),
                        patientServiceDb,
                        Map.of(
                                "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                                "BILLING_SERVICE_GRPC_PORT", "9096"
                        ));

        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);
    }

    private Vpc createVpc(){
        return Vpc.Builder.create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2)
                .build();
    }

    // This instance will be used to create the databases
    private DatabaseInstance createDatabase(String id, String dbName){
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_17_2)
                        .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO)) //instanceType is a combination of cpu, compute power that we want our databases to run on
                .allocatedStorage(20) // the amount of storage we want the databases to run on
                .credentials(Credentials.fromGeneratedSecret("admin_user"))// credentials will be used to connect to the database. This will take the username that we specify and generate a secret from this username and auto apply to the database
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    // health check for databases
    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id){
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort())) // this will get the port that our database is running on. It will convert the string to a number
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30) // check the health status every 30s
                        .failureThreshold(3) // try 3 times once every 30s for every port failure
                        .build())
                .build();
    }

    private CfnCluster createMskCluster(){
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT").build())
                .build();
    }

    private Cluster createEcsCluster(){
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build())
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnVars) {
        // FargateService is a type of ECS service. there are a number of ECS services. It makes it easy to start, stop and scale ECS tasks
        // The ECS task takes image name, and it will build a container from that image name and start the container

        // An ECS task runs a container
        // To create an ECS task, we need to create an ECSTaskDefinition.
        // It's a blueprint that specifies the amount of CPU, memory, the image we want to use, environment variables

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream().map(port -> PortMapping.builder()
                                .containerPort(port)
                                .hostPort(port)
                                .protocol(Protocol.TCP)
                                .build())
                        .collect(Collectors.toList()))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName)
                        .build()));


        // define environment variables
        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");
        if (additionalEnVars != null) {
            envVars.putAll(additionalEnVars);
        }

        if(db != null){
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s/%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));

            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DLL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "600000");
        }

        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        // Now create a Fargate service that will run the ecs task

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();

    }

    public static void main(final String[] args){
        App app = new App(AppProps.builder()
                .outdir("./cdk.out")
                .build());

        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }


}

