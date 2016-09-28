import models.Book;
import models.Product;
import repositories.ProductRepo;

import java.util.List;

/**
 * Created by icart on 7/24/16.
 */
public class CopyToProxy {
    private static final String LANGUAGE = "English";
    private static final ProductRepo repo = new ProductRepo();

    public static void main(String[] args) {
        //repo.clearProxy();
        Main.createTable();
        List<Product> books = repo.getProductsByLang(LANGUAGE);
        repo.saveProductstoProxy(books);
    }
}
