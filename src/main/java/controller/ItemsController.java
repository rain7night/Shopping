package controller;

import validation.ValidGroup1;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pojo.ItemsCustom;
import pojo.ItemsQueryVo;
import service.ItemsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
//定义url的根路径，访问时根路径+方法名的url
@RequestMapping("/items")
public class ItemsController {

    //注入service
    @Autowired
    private ItemsService itemsService;

    //单独将商品类型的方法提取出来，将方法返回值填充到request域，在页面提示
    @ModelAttribute("itemsType")
    public Map<String,String> getItemsType() throws Exception{

        HashMap<String,String> itemsType=new HashMap<>();
        itemsType.put("001","data type");
        itemsType.put("002","clothes");
        return itemsType;
    }

    @RequestMapping("/queryItems")
    @RequiresPermissions("item:query")
    public ModelAndView queryItems() throws Exception {
        //调用servie来查询商品列表
        List<ItemsCustom> itemsList=itemsService.findItemsList(null);

        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("itemsList",itemsList);
        //指定逻辑视图名itemsList.jsp
        modelAndView.setViewName("itemsList");

        return modelAndView;
    }

    //批量修改商品查询
    @RequestMapping("/editItemsList")
    public ModelAndView editItemsList() throws Exception {
        //调用servie来查询商品列表
        List<ItemsCustom> itemsList=itemsService.findItemsList(null);

        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("itemsList",itemsList);
        //指定逻辑视图名itemsList.jsp
        modelAndView.setViewName("editItemsList");

        return modelAndView;
    }

    //批量修改商品的提交

    @RequestMapping("/editItemsListSubmit")
    public String editItemsListSubmit(ItemsQueryVo itemsQueryVo) throws Exception{
        return "success";
    }

    //商品修改页面提示
    //方法返回字符串，字符串就是逻辑视图名，Model作用时将数据填充到request域，在页面显示
    @RequestMapping(value = "/editItems",method = RequestMethod.GET)
    @RequiresPermissions("item:update")//执行此方法需要item:update权限
    public String editItems(Model model, Integer id) throws Exception
    {

        //将id传到页面
        model.addAttribute("id",id);

        //调用service查询商品的信息
        ItemsCustom itemsCustom=itemsService.findItemsById(id);



        model.addAttribute("itemsCustom",itemsCustom);

        return "editItem";
    }

    //更具商品id查看商品信息rest接口
    //@requestMapping中指定restful方式的url中的参数，参数需要用{}包起来
    //@PathVariable将url中的参数和形参进行绑定
    @RequestMapping("/viewItems/{id}")
    public @ResponseBody
    ItemsCustom viewItems(@PathVariable("id") Integer id) throws Exception
    {
        //调用service查询商品的信息
        ItemsCustom itemsCustom=itemsService.findItemsById(id);


        return itemsCustom;
    }

    //商品提交页面
    //itemsQueryVo是包装类型的pojo
    //在@Validated中定义使用ValidGroup1组下边的校验
    @RequestMapping("/editItemSubmit")
    @RequiresPermissions("item:update")//执行此方法需要item:update权限
    public String editItemSubmit(Model model, Integer id,
                                 @Validated(value = {ValidGroup1.class}) @ModelAttribute(value = "itemsCustom") ItemsCustom itemsCustom,
                                 BindingResult bindingResult,
                                 //上传图片
                                 MultipartFile pictureFile
                                 ) throws Exception
    {
        //输出校验错误信息
        //如果参数绑定时出错
        if (bindingResult.hasErrors())
        {
            //获取错误
            List<ObjectError> errors=bindingResult.getAllErrors();

            model.addAttribute("errors",errors);

            for (ObjectError error:errors)
            {
                //输出错误信息
                System.out.println(error.getDefaultMessage());
            }

            //如果校验错误，仍然回到商品修改页面
            return "editItem";

        }


        //进行数据回显
        model.addAttribute("id",id);
//        model.addAttribute("item",itemsCustom);

        //进行图片的上传
        if (pictureFile!=null&&pictureFile.getOriginalFilename()!=null&&pictureFile.getOriginalFilename().length()>0)
        {
            //图片上传成功后，将图片的地址写到数据库
            String filePath="/Pictures/";
            String originalFilename=pictureFile.getOriginalFilename();

            String newFileName= UUID.randomUUID()+originalFilename.substring(originalFilename.lastIndexOf("."));

            //新文件
            File file=new File(filePath+newFileName);

            //将内存中的文件写入磁盘
            pictureFile.transferTo(file);

            //图片上传成功
            itemsCustom.setPic(newFileName);
        }


        itemsService.updateItems(id,itemsCustom);

        //重定向
        return "redirect:queryItems";
    }


    //删除商品
    @RequestMapping("/deleteItems")
    public String deleteItems(Integer[] delete_id) throws Exception
    {
        //调用serive方法删除商品

        return "success";
    }
}
