{ pkgs }: {
  deps = [
    pkgs.systemd
    pkgs.graalvm17-ce
    pkgs.unzip
    pkgs.maven
    pkgs.replitPackages.jdt-language-server
    pkgs.replitPackages.java-debug
    pkgs.xorg.libX11
    pkgs.xorg.libXrender
    pkgs.xorg.libXcomposite
    pkgs.xorg.libXrandr
    pkgs.xorg.libXi
    pkgs.xorg.libXtst
    pkgs.xorg.libXcursor
    pkgs.xorg.libXext
    pkgs.libGL
    pkgs.libGLU
    pkgs.openjfx17
  ];
  env = {
    LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [
      pkgs.xorg.libX11
      pkgs.xorg.libXxf86vm
      pkgs.libGL
      pkgs.xorg.libXtst
      pkgs.libGLU
    ];
  };
}
