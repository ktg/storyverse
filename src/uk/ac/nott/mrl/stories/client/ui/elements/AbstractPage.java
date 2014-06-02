package uk.ac.nott.mrl.stories.client.ui.elements;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Filter;
import org.wornchaos.client.server.FilterListener;
import org.wornchaos.client.server.Filters;
import org.wornchaos.client.server.PaginatedList;

import uk.ac.nott.mrl.stories.model.Server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractPage<T> extends Composite
{
	@SuppressWarnings("rawtypes")
	interface AbstractPageUiBinder extends UiBinder<Widget, AbstractPage>
	{
	}

	private final AsyncCallback<PaginatedList<T>> response = new AsyncCallback<PaginatedList<T>>()
	{
		@Override
		public void onSuccess(final PaginatedList<T> response)
		{
			setPage(response);
		}
	};

	protected final Filters filters = new Filters();

	private static AbstractPageUiBinder uiBinder = GWT.create(AbstractPageUiBinder.class);

	private PaginatedList<T> page;

	protected Server server;

	@UiField
	Panel items;

	@UiField
	Panel pages;

	@UiField
	Panel activeFilters;

	@UiField
	Button prevButton;

	@UiField
	Button nextButton;

	public AbstractPage(final Server server)
	{
		this.server = server;
		initWidget(uiBinder.createAndBindUi(this));

		prevButton.setVisible(false);
		nextButton.setVisible(false);

		getPage(0, new AsyncCallback<PaginatedList<T>>()
		{
			@Override
			public void onSuccess(final PaginatedList<T> response)
			{
				setPage(response);
			}
		});

		filters.add(new FilterListener()
		{
			@Override
			public void filtersChanged()
			{
				getPage(page.getOffset(), response);
				updateActiveFilters();
			}
		});

		updateActiveFilters();
	}

	public void setPage(final PaginatedList<T> page)
	{
		this.page = page;

		// TODO Round up!
		final int pageCount = page.getTotal() / page.getPageSize();
		final int currentPage = page.getOffset() / page.getPageSize();

		items.clear();
		pages.clear();

		prevButton.setVisible(page.getOffset() > 0);
		nextButton.setVisible(pageCount >= 1);

		for (int index = 0; index <= pageCount; index++)
		{
			if (index == currentPage)
			{
				pages.add(new Label("" + (index + 1)));
			}
			else
			{
				final int indexed = index;
				final Anchor anchor = new Anchor("" + (index + 1));
				anchor.addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						getPage(indexed * page.getPageSize(), response);

					}
				});
				pages.add(anchor);
			}
		}

		for (final T item : page.getItems())
		{
			final Widget widget = getWidget(item);
			if (widget != null)
			{
				items.add(widget);
			}
		}
	}

	@UiHandler("nextButton")
	void handleNextPage(final ClickEvent event)
	{
		getPage(page.getOffset() + page.getPageSize(), response);
	}

	@UiHandler("prevButton")
	void handlePrevPage(final ClickEvent event)
	{
		getPage(page.getOffset() - page.getPageSize(), response);
	}

	protected abstract void getPage(int index, AsyncCallback<PaginatedList<T>> response);

	protected abstract Widget getWidget(final T item);

	private void updateActiveFilters()
	{
		activeFilters.clear();

		for (final Filter filter : filters.getFilters())
		{
			activeFilters.add(new Button(filter.getValue(), new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					filters.remove(filter);
				}
			}));
		}
	}
}
