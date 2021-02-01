package org.truenewx.tnxjee.webmvc.region;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.service.spec.region.Region;
import org.truenewx.tnxjee.service.spec.region.RegionSource;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.security.config.annotation.ConfigAnonymous;

@RequestMapping("/region")
public abstract class RegionControllerSupport {

    @Autowired(required = false)
    private RegionSource source;

    @GetMapping("/selectable")
    @ConfigAnonymous
    @ResultFilter(type = Region.class, included = { "group", "code", "caption", "level", "subs" })
    public Collection<Region> selectable(@RequestParam("nation") String nation, HttpServletRequest request) {
        Region region = this.source.getNationalRegion(nation, request.getLocale());
        return region == null ? null : region.getSubs();
    }

}
