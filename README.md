
# Cloud-native, Reactive, live score streaming app development workshop
This repository contains the source code and the resources for workshop from Spring IO 2019, Barcelona.

https://2019.springio.net/sessions/cloud-native-reactive-spring-boot-application-development-workshop

## 03-cloud_contract step

At this step, you are expected to build contract driven REST services. the repository contains live-score-service module which has teamples of contract files and classes

### introduce spring-cloud-contract in the project
in order to be able to use spring-cloud-contract and implement contract driven REST services, we need to add spring-cloud and spring-cloud-contract related stuff in pom.xml

add version properties 'spring-cloud-version' and 'spring-cloud-contract.version'

```
	<properties>
		<java.version>1.8</java.version>
		<spring-cloud.version>Greenwich.SR1</spring-cloud.version>
		<spring-cloud-contract.version>2.1.1.RELEASE</spring-cloud-contract.version>
	</properties>
```
 
add spring cloud dependencies management so that spring cloud starters and dependencies versions can be resolved

```
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```

add spring cloud contract related artifacts in dependencies section

```
	<dependencies>
	    ...
	    ...
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-contract-verifier</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

add spring cloud contract maven plugin in build section

```
	<build>
		<plugins>
		    ...
		    ...

			<plugin>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-contract-maven-plugin</artifactId>
				<version>${spring-cloud-contract.version}</version>
				<extensions>true</extensions>
				<configuration>
					<packageWithBaseClasses>org.springmeetup.livescoreservice</packageWithBaseClasses>
					<testMode>EXPLICIT</testMode>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

### create your first contract (?)
now we start talking about business. until now we have prepared ourselves and our project. talking about business starts with defining the contract of your service that you want to implement.

we will use groovy script file for defining our contract. the contract files should be located under 

```
src
	test
		resources
			contracts 
```

we will group our contracts under package 'api', so we have an other sub-folder 'api' under 'contracts' folder. 

the contract of a typical REST service is composed of following blocks
+ package declaration
+ import section
+ Contract.make { }
+ description of the contract (free format)
+ request section (method : GET/POST?, headers : ?, body : ?)
+ response seciton (status : OK / NOT_FOUND ?, headers : content-type, etc., body : ?)

in the scope of this workshop, we want to POST and GET match details so let's begin with the GET request of a match entity queried by its match-id

```
package contracts.api

import org.springframework.cloud.contract.spec.Contract

Contract.make {
	description("Returns the details of match by id : 1")

	request {
		method GET()
		url "/match/1"
		headers {
			contentType applicationJson()
		}
	}

	response {
		status OK()
		headers {
			contentType applicationJson()
		}
		body("""
			{
				"match-id": 1,
				"name": "Fenerbahçe - Galatasaray",
				"start-date": "2019-05-16T19:00:00",
				"status": "NOT_STARTED"
			}
		"""
		)
	}
}
```

let's run 'mvn test' at this step and see what we get. 

spring-cloud-contract-maven-plugin jumped in at the generate-sources phase. and did 2 things 

first, it has generated automatically test classes from the contracts

```
[INFO] --- spring-cloud-contract-maven-plugin:2.1.1.RELEASE:generateTests (default-generateTests) @ live-score-service ---
[INFO] Generating server tests source code for Spring Cloud Contract Verifier contract verification
[INFO] Will use contracts provided in the folder [/Users/egunay/workspaces-springio/workshop/live-score-service/src/test/resources/contracts]
[INFO] Directory with contract is present at [/Users/egunay/workspaces-springio/workshop/live-score-service/src/test/resources/contracts]
[INFO] Test Source directory: /Users/egunay/workspaces-springio/workshop/live-score-service/target/generated-test-sources/contracts added.
[INFO] Using [null] as base class for test classes, [null] as base package for tests, [org.springmeetup.livescoreservice] as package with base classes, base class mappings []
[INFO] Creating new class file [/Users/egunay/workspaces-springio/workshop/live-score-service/target/generated-test-sources/contracts/org/springmeetup/livescoreservice/ApiTest.java]
[INFO] Generated 1 test classes.
```

and second, it has converted the contract to WireMock stubs mappings and stubs 
```
[INFO] --- spring-cloud-contract-maven-plugin:2.1.1.RELEASE:convert (default-convert) @ live-score-service ---
[INFO] Will use contracts provided in the folder [/Users/egunay/workspaces-springio/workshop/live-score-service/src/test/resources/contracts]
[INFO] Copying Spring Cloud Contract Verifier contracts to [/Users/egunay/workspaces-springio/workshop/live-score-service/target/stubs/META-INF/org.springmeetup/live-score-service/0.0.1-SNAPSHOT/contracts]. Only files matching [.*] pattern will end up in the final JAR with stubs.
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Converting from Spring Cloud Contract Verifier contracts to WireMock stubs mappings
[INFO]      Spring Cloud Contract Verifier contracts directory: /Users/egunay/workspaces-springio/workshop/live-score-service/src/test/resources/contracts
[INFO] Stub Server stubs mappings directory: /Users/egunay/workspaces-springio/workshop/live-score-service/target/stubs/META-INF/org.springmeetup/live-score-service/0.0.1-SNAPSHOT/mappings
[INFO] Creating new stub [/Users/egunay/workspaces-springio/workshop/live-score-service/target/stubs/META-INF/org.springmeetup/live-score-service/0.0.1-SNAPSHOT/mappings/api/get-match-by-id-1.json]
```
after these files are generated, maven continues to compilation and test phases and we get a compilation error at the end 

```
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/egunay/workspaces-springio/workshop/live-score-service/target/generated-test-sources/contracts/org/springmeetup/livescoreservice/ApiTest.java:[11,41] cannot find symbol
  symbol:   class ApiBase
  location: package org.springmeetup.livescoreservice
[ERROR] /Users/egunay/workspaces-springio/workshop/live-score-service/target/generated-test-sources/contracts/org/springmeetup/livescoreservice/ApiTest.java:[20,30] cannot find symbol
  symbol: class ApiBase
[INFO] 2 errors 
```

this is because we have to provide a base class for auto generated test class so that the mock mvc environment can be setup and mock data can be provided for the tests to pass.

at this step, please go ahead and check the generated test class code. you will see that it has all the details from the contract file and asserts the response status and response body fields

```
live-score-service/target/generated-test-sources/contracts/org/springmeetup/livescoreservice/ApiTest.java
```


let's provide the ApiBase class in the following location

```
live-score-service/src/test/java/org/springmeetup/livescoreservice/ApiBase.java
```

the content of ApiBase.java should look like this 

```
package org.springmeetup.livescoreservice;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springmeetup.livescoreservice.service.ApiRestService;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "server.port=0")
public class ApiBase {

	@LocalServerPort
	int port;

	@MockBean
	ApiRestService apiRestService;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost:" + this.port;

	}

}
```

run the test again 'mvn test'. this time we should have passed the compilation failure and as you guessed, we should have received following http status assertion failure

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 16.428 s - in org.springmeetup.livescoreservice.LiveScoreServiceApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[ERROR] Failures: 
[ERROR]   ApiTest.validate_get_match_by_id_1:33 
Expecting:
 <404>
to be equal to:
 <200>
but was not.
```

 
## next section is reactive-redis operations

checkout 04_reactive_redis branch and follow the instructions in README.md

