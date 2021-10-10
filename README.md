# Sakura custom API gateway authorizer
Custom Authorizer in AWS Lambda for API gateway.

## 1. Introduction

This project is a generic implementation to handle authorizations using API gateway. It is based on a DynamoDB table (used as key repo).

## 2. DynamoDB table modeling

The DynamoDB table used to back this authorizer up has the following structure:

- authenticationKey (string) - used as a partition key
- principalId (string) - a principal information
- enabled (boolean) - boolean flag to indicate whether or not such key is active.
