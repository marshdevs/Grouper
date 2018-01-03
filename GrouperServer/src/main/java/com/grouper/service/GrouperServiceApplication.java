package com.grouper.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.regions.Regions;

import com.grouper.objectcache.EventObjectCache;
import com.grouper.objectcache.GroupObjectCache;
import com.grouper.objectcache.UserObjectCache;
import org.hashids.Hashids;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrouperServiceApplication {

    public static AmazonDynamoDB dynamoClient;
    public static Hashids hashids;
    public static UserObjectCache userObjectCache;
    public static GroupObjectCache groupObjectCache;
    public static EventObjectCache eventObjectCache;

    private static void initAWS() throws Exception {

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your " + "credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                e);
        }
        dynamoClient = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(Regions.US_WEST_1)
            .build();
    }

    private static void initHashids() {

        hashids = new Hashids("voBxXOCwSmjtGHYk6mVVzFI2Yr9gbf");
    }

    private static void initObjectCache() {

        UserObjectCache.init();
        GroupObjectCache.init();
        EventObjectCache.init();

        userObjectCache = new UserObjectCache();
        groupObjectCache = new GroupObjectCache();
        eventObjectCache = new EventObjectCache();
    }

    public static void main(String[] args) throws Exception {

        initAWS();
        initHashids();
        initObjectCache();
        SpringApplication.run(GrouperServiceApplication.class, args);
    }

}
