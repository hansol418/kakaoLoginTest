package com.busanit501.springproject3.hjt.controller;

import com.busanit501.springproject3.hjt.domain.HjtEntity;
import com.busanit501.springproject3.hjt.service.HjtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/tool")
public class HjtController {

    @Autowired
    private HjtService hjtService;

    @GetMapping("/list")
    public String listTools(Model model) {
        List<HjtEntity> list = hjtService.findAll();
        model.addAttribute("list", list);
//        log.info("list" + list);
        return "tool/list";
    }

    @GetMapping("/detail/{id}")
    public String toolDetail(@PathVariable Long id, Model model) {
        HjtEntity detail = hjtService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid tool Id:" + id));
        model.addAttribute("detail", detail);
        log.info("detail" + detail);
        return "tool/detail";
        }
    }

