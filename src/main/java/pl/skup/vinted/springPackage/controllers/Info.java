package pl.skup.vinted.springPackage.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skup.vinted.springPackage.services.InfoService;

@Slf4j
@RequestMapping(value = "/info")
@AllArgsConstructor
@RestController
public class Info {
    @Autowired
    InfoService InfoService;

    @GetMapping(path = "/")
    void getBasicInfo() {
        InfoService.getBasicInfo();
    }
}
