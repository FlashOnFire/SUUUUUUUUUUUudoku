{
  lib,
  self,
  stdenv,
  jdk23,
  gradle,
}: let
  jdk = jdk23;
in
  stdenv.mkDerivation (finalAttrs: {
    pname = "SUUUUUUUUUUudoku";
    version = "0.0.0";

    src = ./.;

    nativeBuildInputs = [
      (gradle.override {java = jdk;})
      jdk
    ];

    # # if the package has dependencies, mitmCache must be set
    # mitmCache = gradle.fetchDeps {
    #   inherit (finalAttrs) pname;
    #   data = ./deps.json;
    # };

    gradleFlags = ["-Dfile.encoding=utf-8"];

    # defaults to "assemble"
    # gradleBuildTask = "shadowJar";
    gradleBuildTask = "assemble";

    # will run the gradleCheckTask (defaults to "test")
    doCheck = true;

    installPhase = ''
      mkdir -p $out/{bin,share/suuuuuuuuuudoku}
      cp build/libs/SUUUUUUUUUUUudoku-1.0-SNAPSHOT.jar $out/share/suuuuuuuuuudoku

      makeWrapper ${jdk}/bin/java $out/bin/suuuuuuuuuudoku \
        --add-flags "-jar $out/share/suuuuuuuuuudoku/SUUUUUUUUUUUudoku-1.0-SNAPSHOT.jar"
    '';

    meta.sourceProvenance = with lib.sourceTypes; [
      fromSource
      # binaryBytecode # mitm cache
    ];
  })
