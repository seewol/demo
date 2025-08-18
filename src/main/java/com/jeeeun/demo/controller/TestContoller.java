package com.jeeeun.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController // @Controller + @ResponseBody가 합쳐진 것
public class TestContoller {

    @GetMapping("/hello")
    public String hello(){
        return "Hello, World!";
    }

    @PostMapping("/hello")
    public String postHello(){
        return "[POST] Hello, World!";
    }

    @PutMapping("/hello")
    public String putHello(){
        return "[PUT] Hello, World!";
    }

    @DeleteMapping("/hello")
    public String deleteHello(){
        return "[DELETE] Hello, World!";
    }


    // postman 앱
    // GET http://localhost:8083/hello
    // POST http://localhost:8083/hello
    // PUT http://localhost:8083/hello
    // DELETE http://localhost:8083/hello


    // test/param?name=jeeeun&age=30
    // localhost:8083/test/param?name=jeeeun&age=30
    @GetMapping("/test/param")
    public String requestParam(
       @RequestParam("name") String name,
       @RequestParam("age") Integer age
    ) {
        return "Hello, Request Param, I am " + name + ", age: " + age + ".";
    }

    @GetMapping("test/path/{name}/{age}")
    public String requestPath(
            @PathVariable("name") String name,
            @PathVariable("age") Integer age
    ) {
        return "Hello, Request Path, I am " + name + ", age: " + age + ".";
    }

    // POST, PUT에 RequestBody 사용
    // postman에 localhost:8083/test/body -> Body -> raw, JSON 선택 후 바디 넣고 실행
    @PostMapping("/test/body")
    public String requestBody(
        @RequestBody TestRequestBody request
    ) {
        return "Hello, Request Body, I am " + request.name + ", age: " + request.age + ".";
    }

    public static class TestRequestBody {
        String name;
        Integer age;

        public TestRequestBody(String name, Integer age){
            this.name = name;
            this.age = age;
        }
    }


}
