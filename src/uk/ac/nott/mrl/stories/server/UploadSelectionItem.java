package uk.ac.nott.mrl.stories.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wornchaos.logger.Log;

import uk.ac.nott.mrl.stories.model.Selection;
import uk.ac.nott.mrl.stories.model.SelectionItem;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;

@SuppressWarnings("serial")
public class UploadSelectionItem extends HttpServlet
{
	private final FileService fileService = FileServiceFactory.getFileService();
	private final ImagesService imagesService = ImagesServiceFactory.getImagesService();
	private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private final BlobInfoFactory blobInfoFactory = new BlobInfoFactory();

	private static final OutputSettings output = new OutputSettings(OutputEncoding.JPEG);

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException
	{
		Log.info("Request: " + request.getRequestURL());
		for (final Object key : request.getParameterMap().keySet())
		{
			Log.info("Request: " + key + "=" + request.getParameter((String) key));
		}

		if (request.getParameter("new") != null)
		{
			response.setContentLength(request.getParameter("new").length());
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print(request.getParameter("new"));
			response.getWriter().flush();
		}
		else
		{
			final Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
			final List<BlobKey> blobKeys = blobs.get("image");

			if (blobKeys == null)
			{
				// Uh ... something went really wrong here
			}
			else
			{
				final String id = request.getParameter("id");
				Selection selection = null;
				if (id != null && !id.equals(""))
				{
					selection = ServerImpl.model.getSelection(id);
				}
				if (selection == null)
				{
					selection = new Selection();
				}
				for (final BlobKey blobKey : blobKeys)
				{
					final BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);
					String smallBlobKey = null;

					if (info.getContentType().startsWith("image/"))
					{
						Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
						Transform resize = ImagesServiceFactory.makeResize(640, 480);

						output.setQuality(90);

						Image newImage = imagesService.applyTransform(resize, oldImage, output);

						AppEngineFile file = fileService.createNewBlobFile("image/jpeg", info.getFilename());

						// This time lock because we intend to finalize
						boolean lock = true;
						FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

						// This time we write to the channel directly
						writeChannel.write(ByteBuffer.wrap(newImage.getImageData()));

						// Now finalize
						writeChannel.closeFinally();

						smallBlobKey = fileService.getBlobKey(file).getKeyString();
					}

					final SelectionItem item = new SelectionItem(blobKey.getKeyString(), smallBlobKey);
					if (info != null)
					{
						item.setContentType(info.getContentType());
					}
					ServerImpl.model.setMedia(item);
					selection.add(item);
				}

				ServerImpl.model.setSelection(selection);

				Log.info(request.getRequestURL().toString());
				response.sendRedirect("/upload?new=" + selection.getId());
			}
		}
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
			IOException
	{
		doGet(req, resp);
	}
}