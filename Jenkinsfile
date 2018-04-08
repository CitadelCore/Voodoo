pipeline {
    agent any
	stages {
	    stage("voodoo") {
	        steps {
	            sh './gradlew :voodoo:clean'
	            sh './gradlew :voodoo:build'
	            archive 'voodoo/build/libs/*jar'
	        }
	    }
	    stage("hex") {
	        steps {
	            sh './gradlew :hex:clean'
	            sh './gradlew :hex:build'
	            archive 'hex/build/libs/*jar'
	        }
	    }
	    stage("bootstrap") {
	        steps {
	            sh './gradlew :bootstrap:clean'
	            sh './gradlew :bootstrap:build -Ptarget=voodoo'
	            sh './gradlew :bootstrap:build -Ptarget=hex'
	            archive 'bootstrap/build/libs/*voodoo*'
	            archive 'bootstrap/build/libs/*hex*'
	        }
	    }
	}
}