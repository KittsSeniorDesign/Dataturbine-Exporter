JUDS has to be compiled by hand as it uses a native C library.

```
git clone git@gitlab.com:DecaBot/JUDS.git && cd JUDS
./autoconf.sh && JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./configure
make -j2 && mv juds-*.jar juds.jar
```

put both `juds.jar` and `rbnb.jar` in the top level directory (with the makefile).
Run `make`.
