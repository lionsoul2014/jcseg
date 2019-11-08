<?php
/* A script to convert the csv lexicon to jcseg lexicon */
$baseDir  = dirname(__FILE__);
$src_file = "{$baseDir}/EnWords.csv";
$dst_file = "{$baseDir}/lex-english.lex";

$srcHandler = fopen($src_file, "r");
if ( $srcHandler == false ) {
    exit("Error: Unable to open the source file\n");
}

$dstHandler = fopen($dst_file, "w");
if ( $dstHandler == false ) {
    exit("Error: Unable to open the target file\n");
}

/* Write the first line */
$datetime = date('Y/m/d H:i:s');
fwrite($dstHandler, "CJK_WORD\n");
fwrite($dstHandler, "#Exported from http://github.com/1eez/103976 at {$datetime}\n");

fgets($srcHandler, 1024);  // clear the first line
$line = null;
while ( ($line = fgets($srcHandler, 2049)) != false ) {
    $line = trim($line);
    print("+-process line={$line}\n");

    // find the word
    $sIdx = strpos($line, '"');
    $sIdx += 1;
    $eIdx = strpos($line, '"', $sIdx);
    $word = trim(substr($line, $sIdx, $eIdx - $sIdx));

    // find the translation
    $sIdx = strpos($line, '"', $eIdx + 1);
    $sIdx += 1;
    $eIdx = strrpos($line, '"', -1);
    $tran = trim(substr($line, $sIdx, $eIdx - $sIdx));

    // define the pos
    $pos = 'null';
    for ( $i = 0; $i < 5; $i++ ) {
        if ( $tran[$i] == '.' ) {
            $pos = substr($tran, 0, $i);
            if ( strlen($pos) > 1 ) {
                $pos = "{$pos[0]},{$pos}";
            }
            break;
        }
    }

    print("word={$word}, pos={$pos}\n");
    print("+-Try to write the line to target ... ");
    $item = array(
        $word,      // word item
        $pos,       // part of speech
        'null',     // pinyin
        'null',     // entity
        "\n"
    );

    if ( fwrite($dstHandler, implode('/', $item)) == false ) {
        print(" --[Failed]\n");
        exit();
    }

    print(" --[OK]\n");
}

fclose($srcHandler);
fclose($dstHandler);
