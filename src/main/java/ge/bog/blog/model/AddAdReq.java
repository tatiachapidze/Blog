package ge.bog.blog.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class AddAdReq {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    private double rpm;
    @NotEmpty
    private  String company;
}