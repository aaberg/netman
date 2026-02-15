FROM liquibase/liquibase:5.0.1

# Install the PostgreSQL driver
RUN lpm add postgresql --global

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
CMD ["--changelog-file=liquibase-changelog.xml", "update"]
