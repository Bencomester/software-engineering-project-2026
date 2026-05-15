# Tic-Tac-Toe but better

A következő játékot két játékos játszhatja piros, sárga és zöld kavicsokkal egy
3 × 3 mezőből álló játéktáblán. Felváltva lehet lépni, az alábbi lehetőségek
valamelyikét választva:

- Egy üres mezőre lehet helyezni egy piros kavicsot.
- Egy piros kavicsot ki lehet cserélni egy sárga kavicsra.
- Egy sárga kavicsot ki lehet cserélni egy zöld kavicsra.

Az a játékos nyer, akinek a lépése után valamelyik sorban, oszlopban vagy
átlóban 3 azonos színű kavics lesz.

## Building from Source

Building the project requires JDK 25 or later and access to [GitHub Packages](https://docs.github.com/en/packages).

GitHub Packages requires authentication using a personal access token (classic) that can be created [here](https://github.com/settings/tokens).

> [!IMPORTANT]
> You must create a personal access token (PAT) with the `read:packages` scope.

You need a `settings.xml` file with the following content to store your PAT:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username><!-- Your GitHub username --></username>
            <password><!-- Your GitHub personal access token (classic) --></password>
        </server>
    </servers>
</settings>
```

The `settings.xml` file must be placed in the `.m2` directory in your home directory, i.e., in the same directory that stores your local Maven repository.
