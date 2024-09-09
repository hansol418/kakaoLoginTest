package com.busanit501.springproject3.msy.controller;
///
import com.busanit501.springproject3.msy.dto.BoardDto;
import com.busanit501.springproject3.msy.entity.Comment;
import com.busanit501.springproject3.msy.service.BoardService;
import com.busanit501.springproject3.msy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public String getAllBoards(Model model, Pageable pageable,
                               @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                               @RequestParam(value = "page", defaultValue = "0") int page) {

        // Ensure pageable includes sorting by createDate in descending order
        Pageable sortedPageable = PageRequest.of(page, pageable.getPageSize(), Sort.by("createDate").descending());

        Page<BoardDto> boards;

        // Search keyword is present
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            boards = boardService.findAllItemsByKeyword(searchKeyword, sortedPageable);
        } else {
            boards = boardService.getAllBoards(sortedPageable);
        }

        int nowPage = boards.getNumber(); // 0-based index for current page
        int totalPages = boards.getTotalPages(); // Total number of pages
        long totalElements = boards.getTotalElements(); // Total number of elements

        // Number of page numbers to show
        int pageSize = pageable.getPageSize();

        // Calculate start and end page numbers
        int startPage = Math.max(nowPage - pageSize / 2, 0);
        int endPage = Math.min(startPage + pageSize - 1, totalPages - 1);

        // Adjust startPage if endPage is less than pageSize
        if (endPage - startPage + 1 < pageSize) {
            startPage = Math.max(endPage - pageSize + 1, 0);
        }

        // Calculate starting number for the current page
        long startNumber = totalElements - (nowPage * pageSize);

        model.addAttribute("boards", boards);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("startNumber", startNumber); // Pass the start number to the view

        return "msy/board-list";
    }



    @GetMapping("/{id}")
    public String getBoardById(@PathVariable("id") Long id, Model model) {
        BoardDto board = boardService.getBoardById(id); // Fetch board details

        // Fetch and set comments
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        board.setAnswerList(comments);

        model.addAttribute("board", board); // Add board to model
        return "msy/board-detail"; // View name for board details
    }

    @GetMapping("/new")
    public String createBoardForm(Model model) {
        model.addAttribute("boardDto", new BoardDto());
        return "msy/board-form";
    }

    @PostMapping
    public String createBoard(@ModelAttribute BoardDto boardDto) {
        boardService.createBoard(boardDto);
        return "redirect:/boards";
    }

    @GetMapping("/edit/{id}")
    public String editBoardForm(@PathVariable Long id, Model model) {
        BoardDto board = boardService.getBoardById(id);
        model.addAttribute("boardDto", board);
        return "msy/board-form";
    }

    @PostMapping("/update")
    public String updateBoard(@ModelAttribute BoardDto boardDto, @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        try {
            // Check if file is not empty
            if (!file.isEmpty()) {
                // Save or update the board with file
                boardService.saveOrUpdateItemWithFile(boardDto, file);
            } else {
                // Save or update the board without file
                boardService.saveOrUpdateItem(boardDto);
            }
            redirectAttributes.addFlashAttribute("message", "Board updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "redirect:/boards";
    }

    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boardService.deleteBoard(id);
            redirectAttributes.addFlashAttribute("message", "Board deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete board: " + e.getMessage());
        }
        return "redirect:/boards";
    }
}
