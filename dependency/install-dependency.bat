@echo off

cd /d "%~dp0"

::find all zip file
for %%f in (*.zip) do (
    set "file_name=%%~nf"

    ::unzip zip file
    echo unzip: %%f
    mkdir output\%%~nf
    powershell Expand-Archive %%f -DestinationPath output\%%~nf

    ::check install exist or not
    if exist "output\%%~nf\%%~nf\install.bat" (
        echo install: %%~nf
        cd /d output\%%~nf\%%~nf
        call install.bat
        cd /d "%~dp0"
    ) else (
        cd /d output\%%~nf
        for %%j in (*.jar) do (
            echo install: %%~nj
            mvn install:install-file -Dfile=%%j -DpomFile=%%~nj.pom
        )
        cd /d "%~dp0"
    )
)

pause