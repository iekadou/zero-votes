package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.entities.RecipientList;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.persistence.RecipientListFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.web.util.ZVotesUtils;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.Set;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("recipientListController")
@SessionScoped
public class RecipientListController implements Serializable {

    private RecipientList current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.RecipientListFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public RecipientListController() {
    }

    public RecipientList getSelected() {
        if (current == null) {
            current = new RecipientList();
            selectedItemIndex = -1;
        }
        return current;
    }

    private RecipientListFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

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

    public String prepareList() {
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String prepareView() {
        current = (RecipientList) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new RecipientList();
        
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Organizer current_organizer = userBean.getOrganizer();

        current.setOrganizer(current_organizer);
        
        selectedItemIndex = -1;
        return UrlsPy.RECIPIENTLIST_CREATE.getUrl(true);
    }

    public String create() {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListCreated");
            return prepareCreate();
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (RecipientList) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return UrlsPy.RECIPIENTLIST_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListUpdated");
            return "View";
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (RecipientList) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
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
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public RecipientList getRecipientList(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = RecipientList.class)
    public static class RecipientListControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RecipientListController controller = (RecipientListController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "recipientListController");
            return controller.getRecipientList(getKey(value));
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
            if (object instanceof RecipientList) {
                RecipientList o = (RecipientList) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RecipientList.class.getName());
            }
        }

    }

}
