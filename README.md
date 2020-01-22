RESTful API (including data model and the backing implementation) for
money transfers between accounts.

Explicit requirements:
1. You can use Java or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 and keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require a
pre-installed container/server).
7. Demonstrate with tests that the API works as expected.
Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

#### Build, run tests and package

mvn clean install

The mapping of the URI path space is presented in the following table:

URI path                                     | HTTP methods        | Notes
-------------------------------------------- | ------------------- | --------------------------------------------------------
**_/account/create_**                        | POST                |  Create new account
**_/account/balance/{accountNumber}_**       | GET                 |  Returns a balance
**_/account/add/{accountNumber}_**           | PUT                 |  Add money to an account
**_/account/transfer_**                      | PUT                 |  Transfer money between accounts
**_/account/delete/{accountNumber}_**        | DELETE              |  Delete an account

Start the application

java -jar revtask-1.0-SNAPSHOT.jar <port>
