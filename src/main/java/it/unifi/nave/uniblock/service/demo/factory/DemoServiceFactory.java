/*
 *Copyright (C) 2022-2023 Claudio Nave
 *
 *This file is part of UniBlock.
 *
 *UniBlock is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *UniBlock is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with UniBlock. If not, see <https://www.gnu.org/licenses/>.
 */
package it.unifi.nave.uniblock.service.demo.factory;

import dagger.Component;
import it.unifi.nave.uniblock.service.demo.DemoService;
import javax.inject.Singleton;

@Singleton
@Component(modules = PersistenceModule.class)
public interface DemoServiceFactory {
  DemoService get();
}
