package metier;

import dao.IDao;
import entities.Product;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import util.TestHibernateConfig;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestHibernateConfig.class)
@Transactional
public class ProductDaoImplTest {

    @Autowired
    private IDao<Product> productDao;

    @Test
    @Rollback
    public void testCreateAndFindAll() {
        int initialSize = productDao.findAll().size();

        Product p = new Product();
        p.setName("Test P1");
        p.setPrice(10.5);
        boolean created = productDao.create(p);

        Assert.assertTrue(created);
        List<Product> all = productDao.findAll();
        Assert.assertEquals(initialSize + 1, all.size());
        Assert.assertTrue(p.getId() > 0);
    }

    @Test
    @Rollback
    public void testUpdate() {
        Product p = new Product();
        p.setName("Initial");
        p.setPrice(5.0);
        productDao.create(p);

        p.setName("Updated");
        p.setPrice(7.5);
        boolean ok = productDao.update(p);
        Assert.assertTrue(ok);

        Product loaded = productDao.findById(p.getId());
        Assert.assertEquals("Updated", loaded.getName());
        Assert.assertEquals(7.5, loaded.getPrice(), 0.0001);
    }

    @Test
    @Rollback
    public void testDelete() {
        Product p = new Product();
        p.setName("ToDelete");
        p.setPrice(20);
        productDao.create(p);
        int id = p.getId();

        boolean ok = productDao.delete(p);
        Assert.assertTrue(ok);

        Product loaded = productDao.findById(id);
        Assert.assertNull(loaded);
    }
}
