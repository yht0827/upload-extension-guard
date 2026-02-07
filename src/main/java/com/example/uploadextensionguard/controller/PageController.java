package com.example.uploadextensionguard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.uploadextensionguard.dto.ExtensionResponse;
import com.example.uploadextensionguard.service.ExtensionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

	private final ExtensionService extensionService;

	@GetMapping("/")
	public String index(Model model) {
		ExtensionResponse extensions = extensionService.getAllExtensions();
		model.addAttribute("fixedExtensions", extensions.getFixed());
		model.addAttribute("customExtensions", extensions.getCustom());
		model.addAttribute("customCount", extensions.getCustom().size());
		return "index";
	}
}
