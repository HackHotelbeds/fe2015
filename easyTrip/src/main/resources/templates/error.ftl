<!DOCTYPE html>
<html lang="en">

<head>
    <style>
        body {
            background-image: url('../img/dead-end.jpg');
            background-repeat: no-repeat;
            background-size: cover;
            width: 100%;
            height: 100%;
            overflow: hidden;

            font-family: Helvetica, sans serif;
            color: orange;
            text-shadow: -1px 0 black, 0 2px black, 1px 0 black, 0 -1px black;
        }
        .header {
            display: table;
            position: relative;
            width: 100%;
            height: 100%;
        }
        .text-vertical-center {
            display: table-cell;
            text-align: center;
            vertical-align: middle;
        }

        .text-vertical-center h1 {
            margin: 0;
            padding: 0;
            font-size: 4.5em;
            font-weight: 700;
        }
    </style>
    <meta http-equiv="refresh" content="3;/" />
</head>

<body>
    <header id="top" class="header">
        <div class="text-vertical-center">
            <h1>Something went wrong</h1>
            <h2>${status} ${error}</h2>
        </div>
    </header>
</body>

</html>