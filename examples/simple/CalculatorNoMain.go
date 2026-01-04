package main

import "fmt"

type Calculator struct {
}

func (self *Calculator) add(a int, b int) int {
    return a + b
}
func (self *Calculator) multiply(a int, b int) int {
    return a * b
}
