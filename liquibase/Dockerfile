FROM liquibase/liquibase:5.0.1

# Download and install the PostgreSQL driver manually
RUN curl -L --insecure -o /liquibase/lib/postgresql.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar

# Copy liquibase changelog files into the container
COPY liquibase-changelog.xml /liquibase/changelog/liquibase-changelog.xml
COPY changelog /liquibase/changelog/changelog

# Set the working directory
WORKDIR /liquibase/changelog

# Set default environment variables (can be overridden at runtime)
ENV LIQUIBASE_SEARCH_PATH=/liquibase/changelog
# INSTALL_MYSQL is a base image variable - set to false since we use PostgreSQL

# The entrypoint from the base image will be used
# Command will run the liquibase update with the changelog file
CMD ["update", "--changelog-file=liquibase-changelog.xml"]
