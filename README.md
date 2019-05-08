# kbc-kmx-es
统计es库中风机或风场数据

## 功能
基本功能如下：
- 通过数据类型过滤，根据设备聚合，统计数据个数
- 通过数据类型、设备列表，日期过滤，根据设备聚合，统计数据个数
- 通过数据类型、设备列表，日期过滤，根据日期聚合，统计数据个数
- 通过数据类型、设备列表，日期过滤，根据设备、日期聚合，统计数据个数

## 维护者
liumingchun

## 使用说明
```shell
#环境要求：
#java 8或以上版本
#application.properties添加配置：
kbc.kmx.es.kmx.url=http://10.12.20.36:28090/object-rest
kbc.kmx.es.kmx.casurl=https://10.12.20.36:8443/cas
kbc.kmx.es.kmx.token=yJjdHkiOiJKV1QiLCJlbmMiOiJBMTkyQ0JDLUhTMzg0IiwiYWxnIjoiZGlyIn0..dyqX0ZN_iXwHs5_EzklOow.2186eoylidQXdO1jGJdby-Pfh7LG0aHS5JcHVBPwn01NUay9u2MpTiT1Ba0dqJevMQysyXSIezkiWX4lCyFKt7HML2SkrL5ZeQlsy5O9UwLj_waAmmpTyJgyJ7XZHzs7hnQIDjEOF-E90qse2Ziarh3q5bO8YWBtadJF2fEei0kOdCzZctrBRBVW8xp9vArg4pejjOM72Dpc9i24_W8lRo5BjXIrDIvMg80NGRRebclJqla_Quh71DF68uQ1Usij.SlUW9BM87geWbn6lT5IVItdRq7d6vvwh
kbc.kmx.es.url=http://10.12.20.23:9200/

#pom中操作：
<!-- flummi依赖的netty组件需要只能版本，4.1.3.Final版本有bug -->
<dependencyManagement>
    <dependencies>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-codec-http</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-codec</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-transport</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-buffer</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-common</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-handler</artifactId>
			<version>4.0.52.Final</version>
		</dependency>
		<dependency>
			<groupId>io.netty</groupId>
			<artifactId>netty-transport-native-epoll</artifactId>
			<classifier>linux-x86_64</classifier>
			<version>4.0.52.Final</version>
		</dependency>
	</dependencies>
</dependencyManagement>
<!--kmx平台依赖 -->
<dependency>
	<groupId>com.k2data.platform</groupId>
	<artifactId>k2platform-tools-cas-client</artifactId>
	<version>2.1.0-gw</version>
</dependency>
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.37</version>
</dependency>
<!--flummi依赖 -->
<dependency>
	<groupId>de.otto</groupId>
	<artifactId>flummi</artifactId>
	<version>5.0.32.0</version>
</dependency>
<!-- 需要指定kmx平台组件的下载地址 -->
<repositories>
	<repository>
		<id>KMX</id>
		<name>KMX Maven Repository</name>
		<url>http://124.42.117.178:8081/repository/maven-public</url>
		<layout>default</layout>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
		<releases>
			<enabled>true</enabled>
		</releases>
	</repository>
</repositories>
#样例一：
@Autowired
KmxEsService esService;
...
// 使用方法
esService.queryDeviceAggrWithoutDay(dataCategory);

#样例二
EsService esService = new EsServiceBuilder().build(this.kmxUrl, this.kmxCasUrl, this.kmxToken, this.esUrl);
...
// 使用方法
esService.queryDeviceAggrWithoutDay(dataCategory);