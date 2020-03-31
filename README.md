# maven-repository-aws-s3

**Introduction**

**Installation**



## Configure AWS Pre-Req



### Create IAM



### Create S3 Bucket




## Local PC Setup

on your local maven setup directory ```.m2``` add the following XML snaps in ```setting.xml```. 

```xml
<settings>
  <servers>
    ...
    <server>
      <id>YOUR_BUCKET_NAME-snapshot</id>
      <username>AWS_ACCESS_KEY_ID</username>
      <password>AWS_SECRET_ACCESS_KEY</password>
      <configuration>
        <region>AWS_REGION</region>
        <publicRepository>false</publicRepository>
      </configuration>
    </server>
    <server>
      <id>YOUR_BUCKET_NAME-release</id>
      <username>AWS_ACCESS_KEY_ID</username>
      <password>AWS_SECRET_ACCESS_KEY</password>
      <configuration>
        <region>AWS_REGION</region>
        <publicRepository>false</publicRepository>
      </configuration>
    </server>
    ....
  </servers>
</settings>
```

##### Note: create ```setting.xml``` if it's not exist


## CI/CD Pipeline Setup
