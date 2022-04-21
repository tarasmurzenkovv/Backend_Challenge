Points of improvements

1. Security:
   1. Introduce separate service to handle user authetntication and authorization
   2. Use JWT for stateless security
   3. Create user roles and use these roles to refine access to controllers
   4. We allow CORS for all HTTP methods and for all URLs. We need to narrow scope.
   5. OPTIONS should always work  -- why?????
   6. Define separate bean to use in testing or based on profile
2. Observability
   1. Add logging to std.out
   2. Spring Boot already exposes health and readiness checks --> adapt them to Graphana format?
3. API
   1. Documenation
   2. Pagination for get requests
   3. Mb reactive approach?
   4. Caching requests
   5. Throttling
   6. error responses: do not return plain strings, return pojos with clear error descriptions and urls of resources,
   user ids
4. Deployment
   1. create CD pipeline (Jenkins/Circle CI/GithubAction?)
   2. move all configurations as env variables
   3. create distroless docker image or use jib
   4. create k8s config files (ingress/service/deployment, latter blue-green or canary)
   5. Follow 12 factor principles
5. Testing
   1. I covered only SummationTaskController by integration test: needs to be covered by tests all other services/controllers
   2. Need for load tests
6. DB 
   1. Introduce flyway migration
   2. REad/write replicas
   3. indexes when appropriate
   4. do update in batches to minimize transaction time
   5. hibernate: choose another sequence generator e.x. hi-lo (?)
7. Service
   1. FileService::storeFile is not transactional

The current implementation is not scalable. I suggest the following architecture based on 
- competing consumers patter
- event driven approach. 
- caching requests

1. Introduce the following service: 
   1. API Gateway, 
   2. Customer AUTH, 
   3. Task Submitter, 
   4. Task Processor
   5. Task Status Provider
2. Use as the service: Redis Cached, SQL with master-slave replication (if scalable and if complex data integrity is needed), S3 to store files, 
Debesium to poll transaction log + send events, Queue


1. API Gateway:
   1. Is the gateway to all three service: Submitter, Processor, Status Provider
   2. Implemented in reactive fashion
   3. Delegate customer auth to customer auth service 

2. Task Submitter :
   1. Exposes POST end-point that accepts request body with task description (initial value, limit value)
   2. Creates task id 
   3. Submits create task with task id command to queue topic ``process_task_topic``
   4. Submits initial status task command to queue topic ``task_status_topic``
   5. Immidiatly returns created status
   
3. Task Processor:
   1. Leverages competing consumers pattern: listenes to  ``process_task_topic``
   2. Performs deduplication based on task id
   3. If task is new --> creates new background job based on task information. 
   4. If task is not new --> continies execution: as side effect continuesly pushes task status, value and id to ``task_status_topic``
   5. Uses optimistic locking 
   6. Exposes one DELETE end-point that cancells task execution (as side effect --> sends command to ``task_status_topic``)
   
4. Task Status Provider 
   1. listenes to  ``task_status_topic``, 
   2. accumulates events based on task task id
   3. flushes in batches accumulated events: updates, (at separate DB?), task status, id, accumulated value
   4. Exposes one GET end-point that fetches current task status + caches result from read replica DB