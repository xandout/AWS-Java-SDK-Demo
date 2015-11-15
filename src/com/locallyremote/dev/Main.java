package com.locallyremote.dev;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Tag;
import spark.Spark;


import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Spark.get("/", ((request, response) -> {
                byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(".\\html\\files\\index.html"));
                String s = new String(encoded, Charset.defaultCharset());
                return s;
        }));
        Spark.get("/api/setCreds", (req, res) -> {
            req.session(true);
            req.session().attribute("accessId", req.queryParams("accessId"));
            req.session().attribute("accessKey", req.queryParams("accessKey"));
            return req.session().id();
        });

        Spark.get("/api/getVPCs", (req, res) -> {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(req.session().attribute("accessId"), req.session().attribute("accessKey"));
            SimpleAWS simpleAWS = new SimpleAWS(awsCreds);
            return simpleAWS.getVpcsInAZ(Region.getRegion(Regions.fromName(req.queryParams("region"))));
        }, new JsonTransformer());

        Spark.get("/api/createVPC", (req, res) -> {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(req.session().attribute("accessId"), req.session().attribute("accessKey"));
            SimpleAWS simpleAWS = new SimpleAWS(awsCreds);
            Region region = Region.getRegion(Regions.fromName(req.queryParams("region")));
            List<Tag> tags = new ArrayList<Tag>();
            String[] tagsArray = req.queryParams("tags").split(",");
            for(String s : tagsArray){
                String[] t = s.split(":");
                Tag newTag = new Tag();
                newTag.setKey(t[0]);
                newTag.setValue(t[1]);
                tags.add(newTag);
            }
            return simpleAWS.createVpc(req.queryParams("subnet"), tags ,region).getVpcId();
        });

        Spark.get("/api/deleteVPC", (req, res) -> {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(req.session().attribute("accessId"), req.session().attribute("accessKey"));
            SimpleAWS simpleAWS = new SimpleAWS(awsCreds);
            simpleAWS.deleteVpcById(req.queryParams("vpcId"));
            return "No return value, it probably worked";
        });
    }
}
