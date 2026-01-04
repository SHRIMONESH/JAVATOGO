package main

import "fmt"

type TestAdd struct {
    x int
}

func (self *TestAdd) add(a int, b int) int {
    return a + b
}
func main() {
    test := nil
    test = &TestAdd{}
    result := 0
    result = test.add(5, 3)
}
