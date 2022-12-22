# Containerized TeamSite environment setup

> **DISCLAIMER**: This tutorial assume that your are using a 
> TeamSite Containerized environment provided by *OpenText Technical Marketing Team* 
>
> Use under your own risk :-)

Follow these steps in the server that host your Containerized TeamSite environment

 - Copy the `.jar` file located under the  `output` folder in your server
 
> At the moment of writing this documentation, the latest one is called `OTTeamSiteLSDSRetriever22.2.jar`.
>
> It has been tested with **TeamSite 22.2**
 
 1. Copy all the scripts, `.sh files`, located under the  `scripts` folder in your server
 2. Customize, if needed, the scripts
   - **02_copy_custom_jar.sh** 
       - Check the variable `JAR` value. It's the .jar file name, i.e. OTTeamSiteLSDSRetriever22.2.jar
       - Check the variable `SOURCE_PATH` value. It's the folder where you jave stored the .jar file in your server.
       
```shell
#!/bin/sh
...
JAR="OTTeamSiteLSDSRetriever22.2.jar"
...
SOURCE_PATH="/home/otadmin/Downloads/"
...
```

   - **03_apply_ts_customization.sh**
       - Check the line 18, where we define de Virtual Machine name, without the *.eimdemo.com* extension
   
       
```shell
#!/bin/sh
...
# <vmname> (do not add .eimdemo.com)
./customizehostname.sh vm-demootts 
...
```
 
 3. Open a terminal an execute the following commands:


```shell
./01_clear.sh
./02_copy_custom_jar.sh
./03_apply_ts_customization.sh
```


> NOTE: Once you have upload the script to your server you must give them execution permissions, i.e.  executing `chmod 744 ./*.sh` in a terminal.