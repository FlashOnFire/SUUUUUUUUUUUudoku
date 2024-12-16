{
  description = "A Nix-flake-based Java development environment";

  inputs = {
    nixpkgs.url = "nixpkgs/nixpkgs-unstable";

    flake-parts = {
      url = "github:hercules-ci/flake-parts";
      inputs.nixpkgs-lib.follows = "nixpkgs";
    };
  };

  outputs = {
    self,
    nixpkgs,
    flake-parts,
    ...
  } @ inputs:
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = ["x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin"];

      perSystem = {
        self',
        lib,
        system,
        pkgs,
        ...
      }: {
        packages = {
          default = self'.packages.SUUUUUUUUUUudoku;
          SUUUUUUUUUUudoku = pkgs.callPackage ./package.nix {inherit self;};
        };

        devShells.default = pkgs.mkShell {
          inputsFrom = with self'.packages; [SUUUUUUUUUUudoku];
          # packages = with pkgs; [];
        };

        formatter = pkgs.alejandra;
      };
    };
}
