package uk.ac.nott.mrl.stories.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.nott.mrl.stories.model.SelectionItem;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class GetSelectionItem extends HttpServlet
{
	private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse res) throws IOException
	{
		final String id = req.getParameter("id");
		final boolean thumbnail = Boolean.parseBoolean(req.getParameter("thumb"));
		if (id == null || id.equals("") || id.equals("null")) { return; }
		final SelectionItem media = ServerImpl.model.getMedia(id);
		if (media == null) { return; }
		if (thumbnail && media.getSmallBlobID() != null
				&& (media.getContentType() == null || media.getContentType().startsWith("image/")))
		{
			final BlobKey blobKey = new BlobKey(media.getSmallBlobID());
			blobstoreService.serve(blobKey, res);
		}
		else
		{
			final BlobKey blobKey = new BlobKey(media.getBlobID());
			blobstoreService.serve(blobKey, res);
		}
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
			IOException
	{
		doPost(req, resp);
	}
}
