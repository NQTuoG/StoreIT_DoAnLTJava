package com.example.StoreIT.controller;

import com.example.StoreIT.entity.Item;
import com.example.StoreIT.entity.Product;
import com.example.StoreIT.services.CartService;
import com.example.StoreIT.services.CategoryService;
import com.example.StoreIT.services.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
        private CartService cartService;
    @GetMapping("/products")
    public String showAllProducts(Model model){
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("products/add")
    public String addProductForm(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }
    @PostMapping("products/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, @RequestParam MultipartFile imageProduct, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("product", product);
            return "product/add";
        }

        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                java.nio.file.Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                product.setImage(newImageFile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productService.addProduct(product);
        return "redirect:/products";
    }

    @GetMapping("products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product editproduct = productService.getProductById(id);
        if (editproduct != null) {
            model.addAttribute("product", editproduct);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/edit";
        } else {
            return "redirect:/products";
        }
    }
    @PostMapping("products/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, @ModelAttribute("product") @Valid Product editProduct,@RequestParam MultipartFile imageProduct, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/edit";
        } else {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct != null) {
                existingProduct.setName(editProduct.getName());
                if(imageProduct !=null && imageProduct.getSize()>0)
                {
                    try{
                        File saveFile  = new ClassPathResource("static/images").getFile();
                        String newImageFile = UUID.randomUUID() + ".png";
                        Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+newImageFile);
                        Files.copy(imageProduct.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                        existingProduct.setImage(newImageFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                existingProduct.setPrice(editProduct.getPrice());
                existingProduct.setCategory(editProduct.getCategory());
                productService.updateProduct(existingProduct); // Lưu thay đổi vào cơ sở dữ liệu
            }
            return "redirect:/products";
        }
    }
    @GetMapping("products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            productService.deleteProduct(id);
        }
        return "redirect:/products";
    }
    @GetMapping("products/search")
    public String search(@RequestParam("searchText") String searchText,Model model) {
        List<Product> products = productService.getAllProducts();
        List<Product> filteredProducts = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            filteredProducts = products.stream()
                    .filter(product -> product.getName().contains(searchText))
                    .collect(Collectors.toList());
        }
        model.addAttribute("products", filteredProducts);
        return "product/list";
    }
    @PostMapping("products/add-to-cart")
    public String addToCart(HttpSession session, @RequestParam long id, @RequestParam String name, @RequestParam double price, @RequestParam(defaultValue = "1") int quantity)
    {
        var cart = cartService.getCart(session);
        cart.addItems(new Item(id, name, price, quantity));
        cartService.updateCart(session, cart);
        return "redirect:/products";
    }
}
