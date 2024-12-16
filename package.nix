{
  lib,
  stdenv,
  jdk23,
  gradle,
}: let
  jdk = jdk23;
  self = stdenv.mkDerivation (finalAttrs: {
    pname = "SUUUUUUUUUUUudoku";
    version = "0.0.0";

    src = lib.cleanSource ./.;

    nativeBuildInputs = [
      (gradle.override {java = jdk;})
      jdk
    ];

    mitmCache = gradle.fetchDeps {
      pkg = self;
      data = ./deps.json;
    };

    gradleFlags = ["-Dfile.encoding=utf-8"];

    # defaults to "assemble"
    # gradleBuildTask = "shadowJar";
    gradleBuildTask = "assemble";

    doCheck = true;

    installPhase = ''
      mkdir -p $out/{bin,share/suuuuuuuuuudoku}
      cp build/libs/SUUUUUUUUUUUudoku-1.0-SNAPSHOT.jar $out/share/suuuuuuuuuudoku

      makeWrapper ${jdk}/bin/java $out/bin/suuuuuuuuuudoku \
        --add-flags "-jar $out/share/suuuuuuuuuudoku/SUUUUUUUUUUUudoku-1.0-SNAPSHOT.jar"
    '';

    meta.sourceProvenance = with lib.sourceTypes; [
      fromSource
      binaryBytecode # mitm cache
    ];
  });
in
  self
