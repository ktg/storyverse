package uk.ac.nott.mrl.stories.client;

import uk.ac.nott.mrl.stories.client.ui.places.BoredPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionImagePlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionUploadPlace;
import uk.ac.nott.mrl.stories.client.ui.places.SelectionsPlace;
import uk.ac.nott.mrl.stories.client.ui.places.StoryListPlace;
import uk.ac.nott.mrl.stories.client.ui.places.StoryPlace;
import uk.ac.nott.mrl.stories.client.ui.places.StoryboardPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ StoryListPlace.Tokenizer.class, StoryPlace.Tokenizer.class, StoryboardPlace.Tokenizer.class,
					SelectionUploadPlace.Tokenizer.class, SelectionPlace.Tokenizer.class,
					SelectionImagePlace.Tokenizer.class, SelectionsPlace.Tokenizer.class, BoredPlace.Tokenizer.class })
public interface StoryverseHistoryMapper extends PlaceHistoryMapper
{

}
