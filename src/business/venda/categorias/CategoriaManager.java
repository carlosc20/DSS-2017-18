package business.venda.categorias;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CategoriaManager {
    private static CategoriaManager ourInstance = new CategoriaManager();

    public static CategoriaManager getInstance() {
        return ourInstance;
    }

    private Map<String, Categoria> categorias = new HashMap<>();
    private Map<String, CategoriaObrigatoria> categoriasObrigatorias = new HashMap<>();
    private Map<String, CategoriaOpcional> categoriasOpcionais = new HashMap<>();

    private CategoriaManager() {
        //Categorias Obrigatorias
        categoriasObrigatorias.put("Carrocaria", new Carrocaria());
        categoriasObrigatorias.put("Jantes", new Jantes());
        categoriasObrigatorias.put("Motor", new Motor());
        categoriasObrigatorias.put("Pintura", new Pintura());
        categoriasObrigatorias.put("Pneus", new Pneus());

        //Categorias Opcionais
        categoriasOpcionais.put("Aileron", new Aileron());
        categoriasOpcionais.put("Estofos", new Estofos());
        categoriasOpcionais.put("Farois", new Farois());
        categoriasOpcionais.put("Rádio", new Radio());
        categoriasOpcionais.put("Sistema de som", new SistemaDeSom());
        categoriasOpcionais.put("Tablier", new Tablier());
        categoriasOpcionais.put("Teto", new Teto());

        categorias.putAll(categoriasObrigatorias);
        categorias.putAll(categoriasOpcionais);
    }

    public Categoria getCategoria(String designacao){
        return categorias.get(designacao);
    }

    public CategoriaObrigatoria getCategoriaObrigatoria(String designacao) {
        return categoriasObrigatorias.get(designacao);
    }

    public CategoriaOpcional getCategoriaOpcionais(String designacao) {
        return categoriasOpcionais.get(designacao);
    }

    public Collection<Categoria> getAllCategorias(){
        return categorias.values();
    }

    public Collection<CategoriaObrigatoria> getAllCategoriasObrigatorias(){
        return categoriasObrigatorias.values();
    }

    public Collection<CategoriaOpcional> getAllCategoriasOpcionais(){
        return categoriasOpcionais.values();
    }

    public Set<String> getAllCategoriasDesignacao(){
        return categorias.keySet();
    }

    public Set<String> getAllCategoriasObrigatoriasDesignacao(){
        return categoriasObrigatorias.keySet();
    }

    public Set<String> getAllCategoriasOpcionaisDesignacao(){
        return categoriasOpcionais.keySet();
    }
}
