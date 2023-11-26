SHA = $(shell git rev-parse --short=7 HEAD)
VERSION = $(shell echo "$$(date +'%Y.%-m.%-d')-$(SHA)")

default: test

# Emit the calculated shortform version. Note that this version does not account 
# for pre-release suffixes (-SNAPSHOT) -- it's the "raw" version.
version:
	@echo $(VERSION)

# Clean up the shortform build. Note that this does NOT delete any releases from 
# the `dist` directory. That should be managed manually.
clean:
	sbt clean

# Compile all shortform source code.
compile:
	sbt -Dversion="$(VERSION)" compile

# Run all unit tests.
test:
	sbt -Dversion="$(VERSION)" test

# Run all integration tests.
integration:
	sbt -Dversion="$(VERSION)" "db-integration-tests/test"

# Create a new release distribution of the shortform API.
# On disk, a versioned, compressed tarball will be produced in the `dist`
# directory.
api:
	@echo "Releasing shortform-api: $(VERSION)"
	@mkdir -p dist
	sbt -Drelease=true -Dversion="$(VERSION)" "api / Universal / packageZipTarball"
	@cp ./modules/api/target/universal/shortform-api-$(VERSION).tgz dist/

# TODO: Build container image using buildkit.
release: api
