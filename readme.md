# Overview

java 8, gradle, spring boot, lombok, undertow

# Design decisions & simplifications

* There may be more than one tape with same movie, therefore we rent tapes not movies.
* I didn't focus on very smart billing. Customer is billed for tape, not for the movie as he should be in a real world (when renting two copies of same movie).
* Only one currency.
* A client can pick tapes and bring them to the cashier. Then tapes are identified by unique id (like bar code or some number on it).
* Client brings/picks tape from the shelf therefore there is no need for any additional resource acquiring mechanism.   
* I didn't focus on the database: there is no database, all is stored in memory, therefore no batching is used to retrieve data.
* Command query separation

  Suggested API doesn't handle many of the real-life situations:
   * A client may bring some tapes, ask for the price and then decide he don't want to pay that much. Therefore I decided to change requested API and decouple command (rent tapes) from query (how much for those tapes).
   * Same with giving tapes back. Client may give back the tapes but don't pay for them (no money, complain etc). Therefore command (return tapes) is separated from query (calculate surcharge).
   
   Real world is not transactional therefore separating those API calls allows us handling more scenarios without further affecting the API.
* Movies are separated from movie type. That's because movies and tapes describe the state of our shop while movie type is part of the billing strategy, has nothing to do with physical state and may change in the future.
* Fees and initial times and all details of computing the price is not stored in database. That's because it's a billing strategy (a domain knowledge) that may change at any time into much more complex form unsuitable to store in database.
* No rental history: client returns a tape, system forgets about it (except bonus)  
* No historical offers: changing price and/or movie type will affect the surcharge. More precisely: rental price and initial rental time is calculated using rules (movie type, initial time, movie type) from the rental time. Surcharge is counted for each day after initially payed time. But each day of surcharge will cost according to rules from the return time.
* Price is counted by days, not by 24 hours.
* No timezones.
* Returning movies earlier than declared doesn't mean money return.
* No API for removing/adding items from/to the store.   
* According to specification: bonus check API only at java level (without rest API).
* There is no notification for the client if he can rent a movie for more days for the same money.
* No verbose, detailed error messages.

# RESTful API design

For more detailed examples see `FacadeControllerTest` class.

* Get rental price

POST `/rental-price` with body like:

`[{"tapeId":"matrix_01", "declaredDays":7}, {"tapeId":"spider", "declaredDays":2}]`

    curl -H "content-type:application/json" -d '[{"tapeId":"matrix_01", "declaredDays":7}, {"tapeId":"spider", "declaredDays":2}]' http://localhost:8080/rental-price

* Rent tapes

POST `/rent/{user-id}` with body like:

`[{"tapeId":"matrix_01", "declaredDays":7}, {"tapeId":"spider", "declaredDays":2}]`

    curl -H "content-type:application/json" -d '[{"tapeId":"matrix_01", "declaredDays":7}, {"tapeId":"spider", "declaredDays":2}]' http://localhost:8080/rent/user17

* Calculate surcharge

GET `/surcharge?tapeIds="` with ids separated with comma

    curl http://localhost:8080/surcharge?tapeIds=matrix_01,spider

* Return tapes

POST `/return-tapes?tapeIds=` with ids separated with comma

    curl -X POST http://localhost:8080/return-tapes?tapeIds=matrix_01,spider


# Build, test & run

### Build, run unit and integration tests

    ./gradlew build

### Deployment
It's a standard spring boot application with embedded Undertow so it can be run with `./gradlew bootRun` but for convenience there is a run script:

    cd src/runtime
    ./run.sh

that will run already built application inside maven folder structure. It will use configuration files:

    src/runtime/application.yml

and will place logs and gc statistics in

    build/logs

Application will listen on port 8080.

If you prefer to run application elsewhere you need to change paths in `run.sh` script.

### Sample data

Application starts with sample data loaded. You can see the content in `StartupDataLoader` class.