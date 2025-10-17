package web;

import dao.IDao;
import entities.Product;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import util.HibernateConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    private ApplicationContext context;
    private IDao<Product> productDao;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialiser le contexte Spring et récupérer le DAO
        context = new AnnotationConfigApplicationContext(HibernateConfig.class);
        productDao = context.getBean(IDao.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        List<Product> products = productDao.findAll();

        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><head><title>Produits</title></head><body>");
            out.println("<h1>Liste des produits</h1>");
            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            out.println("<tr><th>ID</th><th>Nom</th><th>Prix</th></tr>");
            for (Product p : products) {
                out.printf("<tr><td>%d</td><td>%s</td><td>%.2f</td></tr>", p.getId(), escape(p.getName()), p.getPrice());
            }
            out.println("</table>");

            out.println("<h2>Ajouter un produit</h2>");
            out.println("<form method='post' action='products'>");
            out.println("Nom: <input type='text' name='name' required /> <br/>");
            out.println("Prix: <input type='number' name='price' step='0.01' required /> <br/>");
            out.println("<button type='submit'>Enregistrer</button>");
            out.println("</form>");

            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String priceStr = req.getParameter("price");

        if (name != null && priceStr != null) {
            try {
                double price = Double.parseDouble(priceStr);
                Product p = new Product();
                p.setName(name);
                p.setPrice(price);
                productDao.create(p);
            } catch (NumberFormatException ignored) {
                // Ignorer entrée invalide pour simplicité
            }
        }
        resp.sendRedirect("products");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    @Override
    public void destroy() {
        if (context instanceof AnnotationConfigApplicationContext) {
            ((AnnotationConfigApplicationContext) context).close();
        }
        super.destroy();
    }
}
