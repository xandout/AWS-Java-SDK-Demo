package com.locallyremote.dev;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import java.util.List;

/**
 * Created by Turner on 11/13/2015.
 */
public class SimpleAWS {
    AWSCredentials _awsCredentials;
    public SimpleAWS(AWSCredentials awsCredentials){
        _awsCredentials = awsCredentials;
    }

    public Vpc createVpc(String subnet, List<Tag> tags, com.amazonaws.regions.Region region){
        CreateVpcRequest createVpcRequest = new CreateVpcRequest(subnet);
        AmazonEC2 amazonEC2 = new AmazonEC2Client(_awsCredentials);
        amazonEC2.setRegion(region);
        CreateTagsRequest createTagsRequest = new CreateTagsRequest();
        CreateVpcResult res = amazonEC2.createVpc(createVpcRequest);
        Vpc vp = res.getVpc();
        createTagsRequest.setTags(tags);
        createTagsRequest.withResources(vp.getVpcId());
        amazonEC2.createTags(createTagsRequest);
        return vp;
    }

    public List<Vpc> getVpcsInAZ(com.amazonaws.regions.Region region){
        AmazonEC2 amazonEC2 = new AmazonEC2Client(_awsCredentials);
        amazonEC2.setRegion(region);
        DescribeVpcsResult vpcsResult = amazonEC2.describeVpcs();
        return vpcsResult.getVpcs();
    }

    public void deleteVpcById(String vpcid){
        AmazonEC2 amazonEC2 = new AmazonEC2Client(_awsCredentials);
        DeleteVpcRequest deleteVpcRequest = new DeleteVpcRequest(vpcid);
        amazonEC2.deleteVpc(deleteVpcRequest);
    }
}
