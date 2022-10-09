package com.main.project.securityConfig.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.project.comment.dto.CommentPatchDto;
import com.main.project.comment.dto.CommentPostDto;
import com.main.project.comment.entity.Comment;
import com.main.project.comment.service.CommentService;
import com.main.project.comment.service.CommentServiceImpl;
import com.main.project.review.dto.ReviewPatchDto;
import com.main.project.review.dto.ReviewPostDto;
import com.main.project.review.entity.Review;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class CheckCommentWriterInterceptor implements HandlerInterceptor {

    CommentServiceImpl commentServiceImpl;

    public CheckCommentWriterInterceptor(CommentServiceImpl commentServiceImpl) {
        this.commentServiceImpl = commentServiceImpl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String httpMethod = request.getMethod();
        String jwtHeader = request.getHeader("Authorization");
        String jwtToken = jwtHeader.replace("Bearer ", "");

        String userId = JWT.require(Algorithm.HMAC512("seb29_main37 jwt token")).build().verify(jwtToken).getClaim("id").toString();


        if (httpMethod.equals("POST")) {//post일 경우 json userId와 토큰 id를 체크한다.

            Map pathvariable  = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            long commentingUserId = Long.parseLong((String) pathvariable.get("user-id"));

            if (commentingUserId!=Long.parseLong(userId)) {
                response.setContentType("text/html; charset=UTF-8"); // 보낼 때 한글 인코딩
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println("인증정보가 올바르지 않습니다!!");
                return false;
            }
        }
        else if(httpMethod.equals("PATCH")){
            Map pathvariable  = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            long commentEdittingUserId = Long.parseLong((String) pathvariable.get("comment-id"));

            long commentedUserId = commentServiceImpl.findComment(commentEdittingUserId).getWebUser().getUserId();

            if(commentedUserId!=Long.parseLong(userId)){
                response.setContentType("text/html; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println("댓글 작성자만 수정 할 수 있습니다.");
                return false;
            }

        } else if (httpMethod.equals("DELETE")) {
            Map pathvariable  = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            long toDeleteId = Long.parseLong((String) pathvariable.get("comment-id"));
            Comment comment = commentServiceImpl.findComment(toDeleteId);

            if(comment.getWebUser().getUserId()!=Long.parseLong(userId)){
                response.setContentType("text/html; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println("댓글 작성자만 삭제할 수 있습니다.");
                return false;
            }
        }

        return true;




    }
}
