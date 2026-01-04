package main

import "fmt"

type Simple struct {
}

func (self *Simple) getValue() int {
    return 42
}
