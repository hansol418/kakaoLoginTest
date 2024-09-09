package com.busanit501.springproject3.msy.controller;
//
import com.busanit501.springproject3.msy.dto.CommentDto;
import com.busanit501.springproject3.msy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/create/{boardId}")
    public String createComment(@PathVariable("boardId") Long boardId, @ModelAttribute CommentDto commentDto) {
        commentDto.setBoardId(boardId);
        commentService.saveComment(commentDto);
        return "redirect:/boards/" + boardId;
    }

    @GetMapping("/comment/edit/{commentId}")
    public String editCommentForm(@PathVariable("commentId") Long commentId, Model model) {
        CommentDto commentDto = commentService.getCommentById(commentId);
        model.addAttribute("commentDto", commentDto);
        return "msy/modifyComment"; // The template for editing comments
    }

    @PostMapping("/comment/update/{commentId}")
    public String updateComment(@PathVariable("commentId") Long commentId, @ModelAttribute CommentDto commentDto) {
        commentService.updateComment(commentId, commentDto);
        return "redirect:/boards/" + commentDto.getBoardId();
    }

    @GetMapping("/comment/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId, RedirectAttributes redirectAttributes) {
        try {
            CommentDto commentDto = commentService.getCommentById(commentId);
            commentService.deleteComment(commentId);
            redirectAttributes.addFlashAttribute("message", "Comment deleted successfully!");
            return "redirect:/boards/" + commentDto.getBoardId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete comment: " + e.getMessage());
            return "redirect:/boards/" + commentId; // Redirect to a safe page if deletion fails
        }
    }

}
