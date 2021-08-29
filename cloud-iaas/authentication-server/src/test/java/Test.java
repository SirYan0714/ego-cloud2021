import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Test {
    @Autowired
    public static PasswordEncoder passwordEncoder;
    public static void main(String[] args) {
        String pwd = "123456";
        String encode = passwordEncoder.encode(pwd);
//        String encodePwd = BCrypt.hashpw(pwd, BCrypt.gensalt()); // 加密，核心代码
        System.out.println(encode);
//        boolean flag = BCrypt.checkpw(pwd, encodePwd); // 验证加密是否正确
//        System.out.println(flag);
    }
}
