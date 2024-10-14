{ pkgs }: {
  deps = [
    pkgs.postgresql
    pkgs.mssql_jdbc
    pkgs.systemd
    pkgs.maven
    pkgs.replitPackages.jdt-language-server
    pkgs.replitPackages.java-debug
    pkgs.libGL
    pkgs.libGLU                     
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
