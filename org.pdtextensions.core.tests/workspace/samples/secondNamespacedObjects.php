<?php
namespace com\example\spaceTwo;
/**
 * some elements in com.example.spaceTwo
 */

function sampleFunctionWithArg($arg1) {
}

function secondFunctionNoArg() {

}

class sampleClass {
    private $privateParam;
    protected $protetedParam;
    public $publicParam;

    private static $privateStaticParam;
    protected static $protectedStaticParam;
    public static $publicStaticParam;
}

class extSampleClass extends sampleClass {
    public $newParam;
}

class sampleInterface {
    public function firstMethod();

    public function secondMethod();
}

abstract class sampleAbstract {
    public function firstMethod();
    abstract public function secondMethod();
}
