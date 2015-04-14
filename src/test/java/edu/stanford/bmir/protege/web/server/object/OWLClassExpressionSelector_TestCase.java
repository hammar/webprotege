package edu.stanford.bmir.protege.web.server.object;

import com.google.common.base.Optional;
import edu.stanford.bmir.protege.web.shared.object.OWLClassExpressionSelector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/02/15
 */
@RunWith(MockitoJUnitRunner.class)
public class OWLClassExpressionSelector_TestCase {

    public static final int BEFORE = -1;

    public static final int AFTER = 1;

    private OWLClassExpressionSelector selector;

    @Mock
    private Comparator<OWLClassExpression> classComparator;

    @Mock
    private OWLClass cls1, cls2;

    @Mock
    private OWLClassExpression clsExpression1, clsExpression2;

    @Before
    public void setUp() throws Exception {
        selector = new OWLClassExpressionSelector(classComparator);
    }


    @Test
    public void shouldNotSelectAnythingForEmptyList() {
        assertThat(selector.selectOne(Collections.<OWLClassExpression>emptyList()),
                is(Optional.<OWLClassExpression>absent()));
    }

    @Test
    public void shouldSelectAbsentForNoClassName() {
        List<OWLClassExpression> input = Arrays.asList(clsExpression1, clsExpression2);
        assertThat(selector.selectOne(input),
                is(Optional.<OWLClassExpression>absent()));
    }

    @Test
    public void shouldSelectSingleOWLClass() {
        List<OWLClassExpression> input = Arrays.asList(clsExpression1, clsExpression2, cls2);
        assertThat(selector.selectOne(input),
                is(Optional.<OWLClassExpression>absent()));
    }

    @Test
    public void shouldSelectSmallerOWLClass() {
        when(cls1.compareTo(cls2)).thenReturn(BEFORE);
        when(cls2.compareTo(cls1)).thenReturn(AFTER);
        List<OWLClassExpression> input = Arrays.asList(cls2, cls1, clsExpression1);
        assertThat(selector.selectOne(input),
                is(Optional.<OWLClassExpression>absent()));
    }
}
