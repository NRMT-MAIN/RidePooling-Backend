# Installation Guide — RidePooling Backend

## Docker
```bash
git clone https://github.com/NRMT-MAIN/RidePooling-Backend.git
cd RidePooling-Backend
docker-compose up build
```


## Manual
Install the following:
- **Java:** JDK 21+ (recommended: 21)
- **Build tool:** Maven 3.8+ 
- **Git:** latest stable
- **Database:** : **MySQL 8+**
```bash
git clone https://github.com/NRMT-MAIN/RidePooling-Backend.git
```
### Configure Environment
The project will typically require DB credentials and server configuration.
Using environment variables:

```bash
PORT=YOUR_PORT_NO
DB_USER=YOUR_DB_USER
DB_PASSWORD=YOUR_DB_PASSWORD
DB_NAME=YOUR_DB_NAME
JWT_SECRET_KEY=KeepThisLongUpto32char
```

### Database Setup
```sql
CREATE DATABASE DB_NAME;
```

### Build the Application

From the repository root:

```bash
mvn clean install
```

If Maven Wrapper exists:

```bash
./mvnw clean install
```


### Run the Application

```bash
mvn spring-boot:run
```

or

```bash
./mvnw spring-boot:run
```


## API Doc  
Swagger/Open API Doc : 
  - `http://localhost:8080/swagger-ui/index.html`

---

