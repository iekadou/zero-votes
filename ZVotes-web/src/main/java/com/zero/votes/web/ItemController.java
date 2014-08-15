package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.ItemFacade;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.ItemType;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.Set;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

@Named("itemController")
@SessionScoped
public class ItemController implements Serializable {

    private Item current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.ItemFacade ejbFacade;
    @EJB
    private com.zero.votes.persistence.ItemOptionFacade ejbOptionFacade;
    @Inject
    private com.zero.votes.web.PollController pollController;
    private PaginationHelper pagination;

    public ItemController() {
    }

    public Item getSelected() {
        if (current == null) {
            current = new Item();
        }
        return current;
    }

    private ItemFacade getFacade() {
        return ejbFacade;
    }

    public void refresh() {
        Item updated_current = getFacade().find(current.getId());
        if (updated_current != null) {
            current = updated_current;
        }
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(1) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList(Poll poll) {
        recreateModel();
        return pollController.prepareEdit(poll);
    }
    
    public String prepareCreate(Poll poll) {
        current = new Item();
        current.setPoll(poll);
        return UrlsPy.ITEM_CREATE.getUrl(true);
    }

    public String create() {
        try {
            getFacade().create(current);
            optionTest(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemCreated");
            return prepareList(current.getPoll());
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareView(Item item) {
        return "TODO";
    }

    public String prepareEdit(Item item) {
        current = item;
        refresh();
        return UrlsPy.ITEM_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            optionTest(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemUpdated");
            return prepareList(current.getPoll());
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy(Item item) {
        current = item;
        Poll poll = current.getPoll();
        performDestroy();
        recreatePagination();
        recreateModel();
        return pollController.prepareEdit(poll);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
        }
    }

    public DataModel getItems() {
        return getPagination().createPageDataModel();
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return UrlsPy.ITEM_LIST.getUrl();
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.ITEM_LIST.getUrl();
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Item getItem(java.lang.Long id) {
        return ejbFacade.find(id);
    }
    
    public ItemType[] getAvailableItemTypes() {
        return ItemType.values();
    }
    
    public void optionTest(Item current) {
        if (current.getType().equals(ItemType.YES_NO)) {
            Set<ItemOption> options = current.getOptions();
            if (options != null && !options.isEmpty()) {
                for (ItemOption option: options) {
                    ejbOptionFacade.remove(option);
                }
            }
            
            ItemOption yes = new ItemOption();
            yes.setShortName("yes");
            yes.setItem(current);
            ejbOptionFacade.create(yes);
            
            ItemOption no = new ItemOption();
            no.setShortName("no");
            no.setItem(current);
            ejbOptionFacade.create(no);
        }
    }

    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getItem(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
