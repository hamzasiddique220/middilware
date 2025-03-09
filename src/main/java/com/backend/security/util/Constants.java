package com.backend.security.util;
public final class Constants {
    public static final int AWS = 1;


    public static final String POST="post";
    public static final String GET="get";
    public static final String DELETE="delete";
    public static final String PUT="put";
    public static final String PATCH="patch";
    public static final String POSTAUTH="postauth";


    public static final int FAIL = 3;
	public static final int COMPLETED = 2;
	public static final int INITIATE = 1;
	public static final int PENDING = 0;


    public static final String INSTANCE = "instance";

    //id
    public static final String PROVIDER_ID = "providerId";
    public static final String INSTANCE_ID = "instanceId";
    public static final String USER_ID = "userId";





	// aws Step
	public static final String AWS_CREATE_NACL_STEP = "awsCreateNacl";
	public static final String AWS_DELETE_NACL_STEP = "awsDeleteNacl";
	public static final String AWS_LIST_IMAGE_STEP = "awsListImages";
	public static final String AWS_CREATE_VPC_STEP = "awsCreateVpc";
	public static final String AWS_DELETE_VPC_STEP = "awsDeleteVpc";
	public static final String AWS_DELETE_BUCKET_STEP = "awsDeleteBucket";
	public static final String AWS_CREATE_VPN_STEP = "awsCreateVpn";
	public static final String AWS_DELETE_VPN_STEP = "awsDeleteVpn";
	public static final String AWS_CREATE_SUBNET_STEP = "awsCreateSubnet";
	public static final String AWS_DELETE_SUBNET_STEP = "awsDeleteSubnet";
	public static final String AWS_CREATE_VPC_PEERING_STEP = "awsCreateVpcPeering";
	public static final String AWS_DELETE_VPC_PEERING_STEP = "awsDeleteVpcPeering";
	public static final String AWS_CREATE_ROUTE_TABLE_STEP = "awsCreateRouteTable";
	public static final String AWS_DELETE_ROUTE_TABLE_STEP = "awsDeleteRouteTable";
	public static final String AWS_CREATE_ROUTE_STEP = "awsCreateRoute";
	public static final String AWS_CREATE_VM_STEP = "awsCreateVM";
	public static final String AWS_DETAIL_VM_STEP = "awsDetailsVM";
	public static final String AWS_DELETE_VM_STEP = "awsDeleteVM";
	public static final String AWS_START_VM_STEP = "awsStartVM";
	public static final String AWS_STOP_VM_STEP = "awsStopVM";
	public static final String AWS_LIST_KEYPAIR_STEP = "awsListKeypair";
	public static final String AWS_CREATE_KEYPAIR_STEP = "awsCreateKeypair";

	public static final String AWS_CREATE_LOADBALANCER_STEP = "awsCreateLoadBalancer";

	public static final String AWS_DELETE_LOADBALANCER_STEP = "awsDeleteLoadBalancer";

	public static final String AWS_CREATE_NAT_GATEWAY_STEP = "awsCreateNatGateway";
	public static final String AWS_DETAIL_NAT_GATEWAY_STEP = "awsDetailNatGateway";
	public static final String AWS_DELETE_NAT_GATEWAY_STEP = "awsDeleteNatGateway";

	public static final String AWS_CREATE_VPC_ENDPOINT_STEP = "awsCreateVpcEndpoint";
	public static final String AWS_DETAIL_VPC_ENDPOINT_STEP = "awsDetailVpcEndpoint";
	public static final String AWS_DETAIL_LOADBALANCER_STEP = "awsDetailLoadBalancer";
	public static final String AWS_DELETE_VPC_ENDPOINT_STEP = "awsDeleteVpcEndpoint";
	public static final String AWS_DELETE_KEYPAIR_STEP = "awsDeleteKeypair";
	public static final String AWS_GET_SECURITY_GROUP_STEP = "awsGetSecurityGroup";
	public static final String AWS_CREATE_SECURITY_GROUP_STEP = "awsCreateSecurityGroup";
	public static final String AWS_DELETE_SECURITY_GROUP_STEP = "awsDeleteSecurityGroup";
	public static final String AWS_SET_INBOUND_SECURITY_GROUP_STEP = "awsSetInboundSecurityGroup";
	public static final String AWS_ALLOCATE_ELASTIC_TP_STEP = "awsAllocateElasticIP";
	public static final String AWS_ASSOCIATE_ELASTIC_IP_STEP = "awsAssociateElasticIP";
	public static final String AWS_DESCRIPTION_VM_STEP = "awsDescriptionVM";
	public static final String AWS_DISASSOCIATE_ELASTIC_TP_STEP = "awsDisassociateElasticIP";
	public static final String AWS_VM_TYPE_STEP = "awsVMType";
	public static final String AWS_RELEASE_ELASTIC_IP_STEP = "awsReleaseElasticIP";
	public static final String AWS_GET_IMAGE_STEP = "awsGetImages";
	public static final String AWS_GET_BILLING_INFO_STEP = "awsGetBillingInfo";
	public static final String AWS_CREATE_VOLUME_STEP = "awsCreateVolume";
	public static final String AWS_UPDATE_VOLUME_STEP = "awsUpdateVolume";
	public static final String AWS_DELETE_VOLUME_STEP = "awsDeleteVolume";
	public static final String AWS_LIST_VOLUME_STEP = "awsDeleteVolume";

	public static final String AWS_CREATE_KEY_PAIR_STEP = "awsCreateKey";
	public static final String AWS_DELETE_KEY_PAIR_STEP = "awsUpdateKey";
	public static final String AWS_UPDATE_KEY_PAIR_STEP = "awsDeleteKey";
	public static final String AWS_LIST_KEY_PAIR_STEP = "awsListKey";



}
