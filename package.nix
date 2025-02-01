{
  lib,
  stdenv,
  jdk23,
  gradle,
  makeWrapper,
  libGL
}: let
  jdk = jdk23;
  self = stdenv.mkDerivation (finalAttrs: {
    pname = "SUUUUUUUUUUUudoku";
    version = "0.0.0";

    src = lib.cleanSource ./.;

    nativeBuildInputs = [
      (gradle.override {java = jdk;})
      jdk
      makeWrapper
    ];

    buildInputs = [
      libGL
    ];

    mitmCache = gradle.fetchDeps {
      pkg = self;
      /*
      To update this file, run:
      nix build .#SUUUUUUUUUUUudoku.mitmCache.updateScript
      ./result
      */
      data = ./deps.json;
    };

    gradleFlags = ["-Dfile.encoding=utf-8"];

    doCheck = true;

    gradleBuildTask = "buildAllJars";

    installPhase = ''
      export LD_LIBRARY_PATH=${lib.makeLibraryPath [libGL]}
      mkdir -p $out/{bin,share/suuuuuuuuuudoku}
      cp build/libs/imGUI-1.0-SNAPSHOT.jar $out/share/suuuuuuuuuudoku
      cp build/libs/swing-1.0-SNAPSHOT.jar $out/share/suuuuuuuuuudoku
      cp build/libs/tui-1.0-SNAPSHOT.jar $out/share/suuuuuuuuuudoku

      makeWrapper ${jdk}/bin/java $out/bin/imGUI \
        --prefix LD_LIBRARY_PATH : ${lib.makeLibraryPath [libGL]} \
        --add-flags "-jar $out/share/suuuuuuuuuudoku/imGUI-1.0-SNAPSHOT.jar"

      makeWrapper ${jdk}/bin/java $out/bin/swing \
        --add-flags "-jar $out/share/suuuuuuuuuudoku/swing-1.0-SNAPSHOT.jar"

      makeWrapper ${jdk}/bin/java $out/bin/tui \
        --add-flags "-jar $out/share/suuuuuuuuuudoku/tui-1.0-SNAPSHOT.jar"
    '';

    meta.sourceProvenance = with lib.sourceTypes; [
      fromSource
      binaryBytecode # mitm cache
    ];
  });
in
  self
